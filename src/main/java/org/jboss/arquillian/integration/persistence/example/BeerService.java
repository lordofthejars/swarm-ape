package org.jboss.arquillian.integration.persistence.example;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Stateless
public class BeerService {

    @PersistenceContext
    EntityManager em;

    public void create(Beer request) {
        em.merge(request);
    }

    public Optional<Beer> findBeerById(Long beerId) {
        return Optional.ofNullable(em.find(Beer.class, beerId));
    }

}
