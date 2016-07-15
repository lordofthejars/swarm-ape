package org.jboss.arquillian.integration.persistence.example;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "beer")
public class Beer implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Beer beer = (Beer) o;

        return id.equals(beer.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
