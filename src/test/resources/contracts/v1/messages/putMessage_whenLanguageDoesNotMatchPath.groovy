package contracts.v1.messages

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return an error code"
    request {
        method 'PUT'
        url '/v1/messages/ru'
        headers {
            contentType(applicationJson())
        }
        body ("""
            {
                "language": "es",
                "content": "Hola Mundo"
            }
        """)
    }
    response {
        status BAD_REQUEST()
    }
}