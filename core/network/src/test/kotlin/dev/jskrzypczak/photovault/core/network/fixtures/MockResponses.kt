package dev.jarkendar.photovault.core.network.fixtures

object MockResponses {

    const val PROBLEM_NOT_FOUND = """
        {
          "type": "https://photovault.local/errors/photo-not-found",
          "title": "Photo Not Found",
          "status": 404
        }
    """

    const val PROBLEM_VALIDATION_FAILED = """
        {
          "type": "https://photovault.local/errors/validation-failed",
          "title": "Validation Failed",
          "status": 400,
          "errors": {
            "name": ["must not be blank"],
            "colorHex": ["must match #RRGGBB"]
          }
        }
    """

    const val PROBLEM_VALIDATION_FAILED_NO_ERRORS = """
        {
          "type": "https://photovault.local/errors/validation-failed",
          "title": "Validation Failed",
          "status": 400
        }
    """

    const val PROBLEM_INVALID_TOKEN = """
        {
          "type": "https://photovault.local/errors/invalid-token",
          "title": "Invalid Token",
          "status": 401
        }
    """

    const val PROBLEM_INVALID_CREDENTIALS = """
        {
          "type": "https://photovault.local/errors/invalid-credentials",
          "title": "Invalid Credentials",
          "status": 401
        }
    """

    const val PROBLEM_FORBIDDEN = """
        {
          "type": "https://photovault.local/errors/forbidden",
          "title": "Forbidden",
          "status": 403
        }
    """

    const val PROBLEM_DUPLICATE_TAG = """
        {
          "type": "https://photovault.local/errors/duplicate-tag-name",
          "title": "Duplicate Tag Name",
          "status": 409,
          "detail": "A tag with name '#morze' already exists"
        }
    """

    const val PROBLEM_INVALID_STATE_TRANSITION = """
        {
          "type": "https://photovault.local/errors/invalid-state-transition",
          "title": "Invalid State Transition",
          "status": 409,
          "detail": "Cannot cancel an upload in 'done' state"
        }
    """

    const val PROBLEM_INTERNAL = """
        {
          "type": "about:blank",
          "title": "Internal Server Error",
          "status": 500
        }
    """

    const val PROBLEM_SERVICE_UNAVAILABLE = """
        {
          "type": "about:blank",
          "title": "Service Unavailable",
          "status": 503
        }
    """
}