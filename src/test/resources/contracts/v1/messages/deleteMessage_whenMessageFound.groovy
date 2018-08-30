package contracts.v1.messages

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should delete the correct message"
    request {
        method 'DELETE'
        url '/v1/messages/en'
    }
    response {
        status NO_CONTENT()
    }
}