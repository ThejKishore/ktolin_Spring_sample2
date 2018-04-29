package com.kish.spring.learning.kotlinreactivetest

import org.reactivestreams.Publisher
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.support.beans
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toFlux

@SpringBootApplication
class KotlinreactivetestApplication

fun main(args: Array<String>) {

    SpringApplicationBuilder()
            .sources(KotlinreactivetestApplication::class.java)
            .initializers(beans{
                bean {
                    ApplicationRunner {
                        val cuRepository = ref<CustomerRepository>()
                        val results: Publisher<Customer> =
                        arrayOf("Thej","Abirami","Shanaya","Kriti")
                                .toFlux()
                                .flatMap { cuRepository.save(Customer(name=it)) }


                        cuRepository.deleteAll()
                                .thenMany(results)
                                .thenMany(cuRepository.findAll())
                                .subscribe{println(it)}
                    }
                }

                bean{
                    router{
                        val customerRepository  = ref<CustomerRepository>()
                        GET("/customers" ) {  ServerResponse.ok().body(customerRepository.findAll())}
                        GET("/customers/{id}") {ServerResponse.ok().body(customerRepository.findById(it.pathVariable("id")))}
                    }
                }
            })
            .run(*args)

//    runApplication<KotlinreactivetestApplication>(*args)
}


interface CustomerRepository : ReactiveMongoRepository<Customer,String>



@Document
data class Customer (var id: String? =null,var name:String? = null)