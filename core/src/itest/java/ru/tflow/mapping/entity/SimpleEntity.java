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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleEntity)) return false;

        SimpleEntity that = (SimpleEntity) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AdvancedSimpleEntity that = (AdvancedSimpleEntity) o;

            if (dt != null ? !dt.equals(that.dt) : that.dt != null) return false;
            if (type != that.type) return false;
            if (url != null ? !url.equals(that.url) : that.url != null) return false;

            return super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (url != null ? url.hashCode() : 0);
            result = 31 * result + (dt != null ? dt.hashCode() : 0);
            return result;
        }
    }

    public static enum Type {
        SIMPLE, ADVANCED
    }

}
