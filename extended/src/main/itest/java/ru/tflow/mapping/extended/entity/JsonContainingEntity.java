package ru.tflow.mapping.extended.entity;

import ru.tflow.mapping.annotations.Id;
import ru.tflow.mapping.extended.annotations.JsonObject;

import java.util.Map;
import java.util.UUID;

/**
 * User: nagakhl
 * Date: 05.06.2014
 * Time: 16:29
 */
public class JsonContainingEntity {

    @Id
    private UUID uid;

    @JsonObject
    private SomeData data;

    @JsonObject
    private Map<String, Object> additionalData;

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public SomeData getData() {
        return data;
    }

    public void setData(SomeData data) {
        this.data = data;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonContainingEntity that = (JsonContainingEntity) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    public static class SomeData {

        private String name;

        private Integer value;

        private Map<String, Object> parameters;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SomeData someData = (SomeData) o;

            if (name != null ? !name.equals(someData.name) : someData.name != null) return false;
            if (parameters != null ? !parameters.equals(someData.parameters) : someData.parameters != null) return false;
            if (value != null ? !value.equals(someData.value) : someData.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
            return result;
        }
    }

}
