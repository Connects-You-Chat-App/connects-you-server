package com.adarsh.connectsYouServer.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class ConflictHttpException : RuntimeException()
