package contracts.v1.messages

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return the updated message"
    request {
        method 'PUT'
        url '/v1/messages/en'
        headers {
            contentType(applicationJson())
        }
        body ("""
            {
                "language": "en",
                "content": "Hello World!"
            }
        """)
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
                "content": "Hello World!"
            }
        """)
    }
}