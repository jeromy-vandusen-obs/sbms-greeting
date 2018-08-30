package contracts.v1.messages

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return a not found error code"
    request {
        method 'DELETE'
        url '/v1/messages/ru'
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status NOT_FOUND()
    }
}