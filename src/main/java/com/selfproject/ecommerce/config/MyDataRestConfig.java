package com.selfproject.ecommerce.config;

import com.selfproject.ecommerce.entity.Country;
import com.selfproject.ecommerce.entity.Product;
import com.selfproject.ecommerce.entity.ProductCategory;
import com.selfproject.ecommerce.entity.State;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    private EntityManager entityManager;

    @Autowired
    public MyDataRestConfig(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, cors);
        HttpMethod[] theUnsupportedActions = {HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE};

        //disable above http methods for product
        disableHttpMethods(Product.class, config, theUnsupportedActions);

        //disable above http methods for product category
        disableHttpMethods(ProductCategory.class, config, theUnsupportedActions);

        disableHttpMethods(Country.class, config, theUnsupportedActions);
        disableHttpMethods(State.class, config, theUnsupportedActions);

        //call an internal helper method
        exposeIds(config);
    }

    private static void disableHttpMethods(Class theClass, RepositoryRestConfiguration config, HttpMethod[] theUnsupportedActions) {
        config.getExposureConfiguration().forDomainType(theClass)
                .withItemExposure(((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions))).withCollectionExposure(((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions)));
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        //expose entity ids

        //get list of all entities from entity manager
        Set<EntityType<?>> entites = entityManager.getMetamodel().getEntities();

        //create an array of entity types
        List<Class> entityClasses = new ArrayList<>();

        //get the entity types
        for(EntityType tempEntityType : entites)
            entityClasses.add(tempEntityType.getJavaType());

        // expose the entity ids for the array of entity types
        Class[]  domainTypes = entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);
    }
}
