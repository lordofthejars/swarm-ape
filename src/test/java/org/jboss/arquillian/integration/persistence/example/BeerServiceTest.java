package org.jboss.arquillian.integration.persistence.example;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.ContainerFactory;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jpa.JPAFraction;
import org.wildfly.swarm.undertow.WARArchive;

import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class BeerServiceTest implements ContainerFactory {

    @Deployment
    public static Archive createDeployment() throws Exception {
        final WARArchive beerArchive = ShrinkWrap.create(WARArchive.class, "beer.war");
        beerArchive.addClasses(Beer.class, BeerService.class);
        beerArchive.addAsWebInfResource(
                new ClassLoaderAsset("persistence.xml",
                    BeerServiceTest.class.getClassLoader()),
                    "classes/META-INF/persistence.xml");

        beerArchive.addAllDependencies();

        System.out.println(beerArchive.toString(true));

        return beerArchive;
    }

    @Override
    public Container newContainer(String... strings) throws Exception {
        return createContainer();
    }

    private static Container createContainer() throws Exception {

        Container container = new Container();

        container.fraction(new DatasourcesFraction()
                .jdbcDriver("h2", (d) -> {
                    d.driverClassName("org.h2.Driver");
                    d.xaDatasourceClass("org.h2.jdbcx.JdbcDataSource");
                    d.driverModuleName("com.h2database.h2");
                })
                .dataSource("beers", (ds) -> {
                    ds.driverName("h2");
                    ds.connectionUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
                    ds.userName("sa");
                    ds.password("sa");
                })
        );

        // Prevent JPA Fraction from installing it's default datasource fraction
        container.fraction(new JPAFraction()
                .inhibitDefaultDatasource()
                .defaultDatasource("jboss/datasources/beers")
        );

        return container;
    }

    @EJB
    BeerService beerService;

    @Test
    @ShouldMatchDataSet("expected-beers.yml")
    public void test() {
        Beer beer = new Beer();
        beer.setId(1L);
        beer.setName("My Best Beer");

        beerService.create(beer);
    }



}
