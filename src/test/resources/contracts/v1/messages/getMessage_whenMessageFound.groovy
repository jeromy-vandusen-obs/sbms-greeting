package contracts.v1.messages

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return the correct message"
    request {
        method 'GET'
        url '/v1/messages/en'
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
            {
                "id": "1",
                "language": "en",
                "content": "Hello World"
            }
        """)
    }
}