#!/usr/bin/env python3
"""
Analyze Jetpack Compose compiler stability reports.
Parses *-composables.txt files and identifies restartable but non-skippable composables.
"""

import argparse
import json
import os
import re
import sys
from dataclasses import dataclass, field
from pathlib import Path


# ── Data classes ──────────────────────────────────────────────────────────────

@dataclass
class UnstableParam:
    name: str
    type: str
    reason: str


@dataclass
class ComposableIssue:
    function_name: str
    file_hint: str          # module/package extracted from report path
    is_restartable: bool
    is_skippable: bool
    unstable_params: list[UnstableParam] = field(default_factory=list)
    raw_block: str = ""


# ── Parsing ───────────────────────────────────────────────────────────────────

# Known types that are never stable by default (but often OK by design)
KNOWN_DESIGN_UNSTABLE = {
    "ViewModel", "Context", "Activity", "Fragment",
    "NavController", "NavHostController",
}

def classify_instability_reason(param_type: str) -> str:
    """Return a human-readable reason why a param type is unstable."""
    t = param_type.strip()

    if t in KNOWN_DESIGN_UNSTABLE:
        return f"`{t}` is intentionally unstable (by design — OK to ignore)"
    if re.search(r"List<|ArrayList<|MutableList<", t):
        return f"`{t}` — use `ImmutableList` from `kotlinx.collections.immutable`"
    if re.search(r"Map<|HashMap<|MutableMap<", t):
        return f"`{t}` — use `ImmutableMap` from `kotlinx.collections.immutable`"
    if re.search(r"Set<|HashSet<|MutableSet<", t):
        return f"`{t}` — use `ImmutableSet` from `kotlinx.collections.immutable`"
    if t.startswith("() ->") or "->" in t:
        return f"`{t}` — lambda captured in composition; wrap in `remember {{ }}`"
    if t.startswith("Flow<") or t.startswith("StateFlow<") or t.startswith("SharedFlow<"):
        return f"`{t}` — collect in ViewModel, pass stable State instead"
    return f"`{t}` — add `@Stable` or `@Immutable` annotation, or wrap in a stable data class"


def parse_composables_file(path: Path) -> list[ComposableIssue]:
    """Parse a single *-composables.txt file and return issues."""
    text = path.read_text(encoding="utf-8")
    issues: list[ComposableIssue] = []

    # Each composable block starts with "restartable" or "non-restartable"
    # Format example:
    #   restartable skippable scheme("[androidx.compose.ui.UiComposable]") fun MyButton(
    #     stable onClick: () -> Unit
    #     stable text: String
    #   )
    #
    #   restartable scheme("[androidx.compose.ui.UiComposable]") fun MyCard(
    #     unstable items: List<Item>
    #     stable modifier: Modifier?
    #   )

    block_pattern = re.compile(
        r"(restartable|non-restartable)(.*?)fun\s+(\w+)\s*\((.*?)\)",
        re.DOTALL,
    )

    for match in block_pattern.finditer(text):
        prefix = match.group(1)           # "restartable" / "non-restartable"
        flags = match.group(2)            # " skippable scheme(...) "
        func_name = match.group(3)
        params_block = match.group(4)

        is_restartable = prefix == "restartable"
        is_skippable = "skippable" in flags

        # We only care about restartable + non-skippable
        if not is_restartable or is_skippable:
            continue

        # Parse unstable params
        unstable_params: list[UnstableParam] = []
        param_pattern = re.compile(r"(unstable)\s+(\w+):\s*([^\n]+)")
        for p in param_pattern.finditer(params_block):
            pname = p.group(2)
            ptype = p.group(3).strip()
            reason = classify_instability_reason(ptype)
            unstable_params.append(UnstableParam(name=pname, type=ptype, reason=reason))

        # Skip if all instability is "by design" (ViewModel etc.)
        actionable_params = [
            p for p in unstable_params
            if "by design" not in p.reason
        ]

        issues.append(ComposableIssue(
            function_name=func_name,
            file_hint=path.stem.replace("-composables", ""),
            is_restartable=is_restartable,
            is_skippable=is_skippable,
            unstable_params=unstable_params,
            raw_block=match.group(0),
        ))

    return issues


def find_report_files(reports_dir: Path) -> list[Path]:
    """Recursively find all *-composables.txt files."""
    return list(reports_dir.rglob("*-composables.txt"))


# ── Report generation ─────────────────────────────────────────────────────────

def build_markdown_report(
    all_issues: list[ComposableIssue],
    report_files: list[Path],
) -> str:
    lines = ["<!-- compose-stability-report -->"]

    actionable = [
        i for i in all_issues
        if any("by design" not in p.reason for p in i.unstable_params)
        or not i.unstable_params
    ]
    design_only = [
        i for i in all_issues
        if i not in actionable
    ]

    if not all_issues:
        lines += [
            "## ✅ Compose Stability Check — Passed",
            "",
            "All composables are either skippable or non-restartable. No stability issues found.",
        ]
        return "\n".join(lines)

    lines += [
        f"## {'❌' if actionable else '⚠️'} Compose Stability Check",
        "",
    ]

    if actionable:
        lines += [
            f"Found **{len(actionable)}** composable(s) that are `restartable` but **not `skippable`** — "
            "these will recompose unnecessarily when the parent recomposes.",
            "",
            "> 💡 **Artifact** with full compiler reports is attached to this run.",
            "",
        ]

        # Group by module
        by_module: dict[str, list[ComposableIssue]] = {}
        for issue in actionable:
            by_module.setdefault(issue.file_hint, []).append(issue)

        for module, issues in sorted(by_module.items()):
            lines += [f"### 📦 `{module}`", ""]
            for issue in issues:
                lines += [f"#### `{issue.function_name}`", ""]
                if issue.unstable_params:
                    lines.append("| Parameter | Type | Fix |")
                    lines.append("|-----------|------|-----|")
                    for p in issue.unstable_params:
                        if "by design" not in p.reason:
                            lines.append(f"| `{p.name}` | `{p.type}` | {p.reason} |")
                else:
                    lines.append(
                        "_Composable has no unstable params detected — "
                        "check if a called composable or captured lambda is causing instability._"
                    )
                lines.append("")

    if design_only:
        lines += [
            "<details>",
            f"<summary>⚠️ {len(design_only)} composable(s) with intentionally unstable params (OK to ignore)</summary>",
            "",
            "These composables receive `ViewModel`, `Context`, `NavController` etc. "
            "which are unstable by design. They won't benefit from skippability anyway.",
            "",
        ]
        for issue in design_only:
            params_str = ", ".join(f"`{p.name}: {p.type}`" for p in issue.unstable_params)
            lines.append(f"- `{issue.function_name}` in `{issue.file_hint}` — {params_str}")
        lines += ["", "</details>", ""]

    lines += [
        "---",
        "<details>",
        "<summary>📖 How to fix stability issues</summary>",
        "",
        "**Collections** — replace `List<T>` / `Map<K,V>` with:",
        "```kotlin",
        'implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")',
        "```",
        "```kotlin",
        "// Before",
        "fun MyList(items: List<Item>)",
        "// After",
        "fun MyList(items: ImmutableList<Item>)",
        "```",
        "",
        "**Custom classes** — annotate with `@Stable` or `@Immutable`:",
        "```kotlin",
        "@Immutable",
        "data class UiState(val name: String, val count: Int)",
        "```",
        "",
        "**Lambdas** — wrap in `remember` at call site or hoist to stable reference:",
        "```kotlin",
        "val onClick = remember { { viewModel.onButtonClick() } }",
        "MyButton(onClick = onClick)",
        "```",
        "",
        "**Flows** — collect in ViewModel, pass `State<T>` instead of `Flow<T>`.",
        "</details>",
    ]

    return "\n".join(lines)


# ── Main ──────────────────────────────────────────────────────────────────────

def main():
    parser = argparse.ArgumentParser(description="Analyze Compose compiler stability reports")
    parser.add_argument("--reports-dir", required=True, help="Directory containing compiler reports")
    parser.add_argument("--output-file", default="stability_report.md", help="Markdown output path")
    parser.add_argument("--json-output", default="stability_report.json", help="JSON summary output path")
    args = parser.parse_args()

    reports_dir = Path(args.reports_dir)

    if not reports_dir.exists():
        print(f"⚠️  Reports directory not found: {reports_dir}", file=sys.stderr)
        print("This likely means the build failed before generating reports.")
        # Write empty-ish report so workflow can still comment
        Path(args.output_file).write_text(
            "<!-- compose-stability-report -->\n"
            "## ❌ Compose Stability Check — Build Failed\n\n"
            "The build did not complete successfully. No stability reports were generated.\n"
            "Check the build logs for compilation errors.\n"
        )
        Path(args.json_output).write_text(json.dumps({"unstable_count": 0, "error": "build_failed"}))
        sys.exit(0)  # Don't double-fail — build step already failed

    report_files = find_report_files(reports_dir)
    print(f"Found {len(report_files)} composables report file(s)")

    all_issues: list[ComposableIssue] = []
    for f in report_files:
        print(f"  Parsing: {f.relative_to(reports_dir)}")
        issues = parse_composables_file(f)
        all_issues.extend(issues)
        print(f"    → {len(issues)} restartable non-skippable composable(s)")

    actionable = [
        i for i in all_issues
        if any("by design" not in p.reason for p in i.unstable_params)
        or not i.unstable_params
    ]

    markdown = build_markdown_report(all_issues, report_files)
    Path(args.output_file).write_text(markdown, encoding="utf-8")
    print(f"\nMarkdown report written to: {args.output_file}")

    summary = {
        "total_composables_checked": len(report_files),
        "unstable_count": len(actionable),
        "design_unstable_count": len(all_issues) - len(actionable),
        "issues": [
            {
                "function": i.function_name,
                "module": i.file_hint,
                "unstable_params": [
                    {"name": p.name, "type": p.type, "reason": p.reason}
                    for p in i.unstable_params
                    if "by design" not in p.reason
                ],
            }
            for i in actionable
        ],
    }
    Path(args.json_output).write_text(json.dumps(summary, indent=2, ensure_ascii=False))
    print(f"JSON summary written to: {args.json_output}")

    if actionable:
        print(f"\n❌ {len(actionable)} actionable stability issue(s) found.", file=sys.stderr)
        sys.exit(1)
    else:
        print(f"\n✅ No actionable stability issues found.")
        sys.exit(0)


if __name__ == "__main__":
    main()