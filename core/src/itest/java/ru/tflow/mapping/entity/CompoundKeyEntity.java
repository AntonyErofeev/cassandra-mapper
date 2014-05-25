package ru.tflow.mapping.entity;

import ru.tflow.mapping.annotations.Composite;
import ru.tflow.mapping.annotations.Id;
import ru.tflow.mapping.annotations.Table;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by nagakhl on 3/25/2014.
 */
@Table("conmpound")
public class CompoundKeyEntity {

    @Id
    public UUID clusteringId;

    @Composite(0)
    public ZonedDateTime time;

    @Composite(1)
    public int partId;

    public BigDecimal number;

    public InetAddress address;

    public UUID getClusteringId() {
        return clusteringId;
    }

    public void setClusteringId(UUID clusteringId) {
        this.clusteringId = clusteringId;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompoundKeyEntity)) return false;

        CompoundKeyEntity that = (CompoundKeyEntity) o;

        if (partId != that.partId) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (clusteringId != null ? !clusteringId.equals(that.clusteringId) : that.clusteringId != null) return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clusteringId != null ? clusteringId.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + partId;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
