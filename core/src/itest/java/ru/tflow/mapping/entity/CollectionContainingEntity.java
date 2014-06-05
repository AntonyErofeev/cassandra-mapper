package ru.tflow.mapping.entity;

import ru.tflow.mapping.annotations.Id;

import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
public class CollectionContainingEntity {

    @Id
    private UUID uid;

    private List<Integer> intList;

    private List<ZonedDateTime> dateTimeList;

    private Set<Instant> instantSet;

    private Set<URL>  urlSet;

    private Map<String, MapType> stringKeyedMap;

    private Map<UUID, ByteBuffer> uidMappedMap;

    public static enum MapType {
        ONE, TWO, THREE
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public List<Integer> getIntList() {
        return intList;
    }

    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }

    public List<ZonedDateTime> getDateTimeList() {
        return dateTimeList;
    }

    public void setDateTimeList(List<ZonedDateTime> dateTimeList) {
        this.dateTimeList = dateTimeList;
    }

    public Set<Instant> getInstantSet() {
        return instantSet;
    }

    public void setInstantSet(Set<Instant> instantSet) {
        this.instantSet = instantSet;
    }

    public Set<URL> getUrlSet() {
        return urlSet;
    }

    public void setUrlSet(Set<URL> urlSet) {
        this.urlSet = urlSet;
    }

    public Map<String, MapType> getStringKeyedMap() {
        return stringKeyedMap;
    }

    public void setStringKeyedMap(Map<String, MapType> stringKeyedMap) {
        this.stringKeyedMap = stringKeyedMap;
    }

    public Map<UUID, ByteBuffer> getUidMappedMap() {
        return uidMappedMap;
    }

    public void setUidMappedMap(Map<UUID, ByteBuffer> uidMappedMap) {
        this.uidMappedMap = uidMappedMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectionContainingEntity that = (CollectionContainingEntity) o;

        if (dateTimeList != null ? !dateTimeList.equals(that.dateTimeList) : that.dateTimeList != null) return false;
        if (instantSet != null ? !instantSet.equals(that.instantSet) : that.instantSet != null) return false;
        if (intList != null ? !intList.equals(that.intList) : that.intList != null) return false;
        if (stringKeyedMap != null ? !stringKeyedMap.equals(that.stringKeyedMap) : that.stringKeyedMap != null) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        if (uidMappedMap != null ? !uidMappedMap.equals(that.uidMappedMap) : that.uidMappedMap != null) return false;
        if (urlSet != null ? !urlSet.equals(that.urlSet) : that.urlSet != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (intList != null ? intList.hashCode() : 0);
        result = 31 * result + (dateTimeList != null ? dateTimeList.hashCode() : 0);
        result = 31 * result + (instantSet != null ? instantSet.hashCode() : 0);
        result = 31 * result + (urlSet != null ? urlSet.hashCode() : 0);
        result = 31 * result + (stringKeyedMap != null ? stringKeyedMap.hashCode() : 0);
        result = 31 * result + (uidMappedMap != null ? uidMappedMap.hashCode() : 0);
        return result;
    }
}
