package contracts.v1.messages

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should store the provided message"
    request {
        method 'PUT'
        url '/v1/messages/es'
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
        status CREATED()
        headers {
            contentType(applicationJson())
        }
        body("""
            {
                "id": "4",
                "language": "es",
                "content": "Hola Mundo"
            }
        """)
    }
}