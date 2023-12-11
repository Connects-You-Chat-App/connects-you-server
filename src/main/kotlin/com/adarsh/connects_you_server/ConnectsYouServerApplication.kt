package com.adarsh.connects_you_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ConnectsYouServerApplication

fun main(args: Array<String>) {
    runApplication<ConnectsYouServerApplication>(*args)
}


/**
 * store user signup time secret in drive and store in our flutter secure storage after every login
 * send user public key as it is,
 * send user private key encrypted with user's secret key
 *
 * store user generated secret key with other users are store in separate table with userId and also it is encrypted with user's secret key
 */