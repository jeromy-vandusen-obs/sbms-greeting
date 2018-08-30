package contracts.v1.messages

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return a list of messages"
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
                {
                    "id": "1",
                    "language": "en",
                    "content": "Hello World"
                },
                {
                    "id": "2",
                    "language": "fr",
                    "content": "Bonjour Monde"
                },
                {
                    "id": "3",
                    "language": "pt",
                    "content": "Olá Mondo"
                }
            ]
        """)
    }
}