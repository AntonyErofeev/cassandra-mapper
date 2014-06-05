package ru.tflow.mapping.extended.repository;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import ru.tflow.mapping.extended.ExtendedMapperConfiguration;

import java.util.Arrays;
import java.util.List;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
public class ConfigurationHolder {

    private static ExtendedMapperConfiguration configuration = new ExtendedMapperConfiguration() {

        private Cluster cluster;

        private List<String> contactPoints = Arrays.asList("127.0.0.1");

        private String keyspace = "test_keyspace";

        private Session session;

        @Override
        public Session session() {
            return session == null ? initSession() : session;
        }

        @Override
        public String keyspace() {
            return keyspace;
        }

        @Override
        public boolean failOnUpdate() {
            return false;
        }

        protected Session initSession() {
            Cluster.Builder builder = Cluster.builder();
            this.contactPoints.forEach(builder::addContactPoint);
            cluster = builder.build();
            session = cluster.connect(keyspace);
            return session;
        }
    };

    public static ExtendedMapperConfiguration configuration() {
        return configuration;
    }

}
