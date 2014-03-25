package ru.tflow.mapping.entity;

import ru.tflow.mapping.annotations.Field;
import ru.tflow.mapping.annotations.Id;
import ru.tflow.mapping.annotations.Table;
import ru.tflow.mapping.annotations.Transitional;

import javax.swing.text.html.parser.Entity;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
@Table("simple")
public class SimpleEntity {

    @Id
    private UUID key;

    @Field("entity_name")
    private String name;

    private ByteBuffer data;

    @Transitional
    private String tString;

    public UUID getKey() {
        return key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ByteBuffer getData() {
        return data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    public String gettString() {
        return tString;
    }

    public void settString(String tString) {
        this.tString = tString;
    }

    @Table("simple")
    public static class AdvancedSimpleEntity extends SimpleEntity {

        private Type type;

        private URL url;

        private ZonedDateTime dt;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }

        public ZonedDateTime getDt() {
            return dt;
        }

        public void setDt(ZonedDateTime dt) {
            this.dt = dt;
        }
    }

    public static enum Type {
        SIMPLE, ADVANCED
    }

}
