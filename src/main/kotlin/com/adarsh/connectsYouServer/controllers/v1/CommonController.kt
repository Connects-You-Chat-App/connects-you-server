package com.adarsh.connectsYouServer.controllers.v1

import com.adarsh.connectsYouServer.annotations.AuthRequired
import com.adarsh.connectsYouServer.models.common.UserJWTClaim
import com.adarsh.connectsYouServer.services.v1.CommonService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat

@RestController
@RequestMapping(Constants.VERSION_PREFIX)
class CommonController(
    private val commonService: CommonService,
) {
    @AuthRequired
    @GetMapping("/updated-data/{updatedAt}")
    fun getUpdatedData(
        @RequestAttribute("user") user: UserJWTClaim,
        @PathVariable("updatedAt") updatedAt: String,
    ): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            commonService.getUpdatedData(
                user,
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(updatedAt),
            ),
        )
    }
}
