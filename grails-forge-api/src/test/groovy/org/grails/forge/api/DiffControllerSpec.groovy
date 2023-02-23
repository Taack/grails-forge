package org.grails.forge.api


import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.grails.forge.application.ApplicationType
import spock.lang.Specification

@MicronautTest
class DiffControllerSpec extends Specification {

    @Inject
    DiffClient diffClient

    void 'test diff app'() {
        when:
        def result = diffClient.diffApp(
                ApplicationType.WEB,
                "test",
                ["gorm-mongodb"],
                null,
                null,
                null,
                null
        )

        then:
        result.contains('+## Feature gorm-mongodb documentation')
    }

    void 'test diff feature'() {
        when:
        def result = diffClient.diffFeature(
                ApplicationType.WEB,
                null,
                "gorm-mongodb",
                null,
                null,
                null,
                null
        )

        then:
        result.contains('+## Feature gorm-mongodb documentation')
    }

    void 'test diff invalid feature'() {
        when:
        diffClient.diffFeature(
                ApplicationType.WEB,
                null,
                "junkkkkk",
                null,
                null,
                null,
                null
        )

        then:
        def e = thrown(HttpClientResponseException)
        e.status == HttpStatus.BAD_REQUEST
        e.getResponse().getBody(Map).get()._embedded.errors[0].message == 'The requested feature does not exist: junkkkkk'
    }
}
