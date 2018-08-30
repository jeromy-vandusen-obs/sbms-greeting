package contracts.v1.messages

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return an empty list"
    request {
        method 'GET'
        url '/v1/messages'
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body("""
            [
            ]
        """)
    }
}