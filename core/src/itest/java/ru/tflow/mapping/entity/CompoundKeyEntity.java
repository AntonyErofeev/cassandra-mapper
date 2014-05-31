package ru.tflow.mapping.entity;

import ru.tflow.mapping.annotations.Compound;
import ru.tflow.mapping.annotations.Id;
import ru.tflow.mapping.annotations.Table;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
@Table("compound")
public class CompoundKeyEntity {

    @Id
    public UUID clusteringId;

    @Compound(0)
    public ZonedDateTime time;

    @Compound(1)
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

        return partId == that.partId
            && !(address != null ? !address.equals(that.getAddress()) : that.address != null)
            && !(clusteringId != null ? !clusteringId.equals(that.getClusteringId()) : that.clusteringId != null)
            && !(number != null ? !number.equals(that.getNumber()) : that.number != null)
            && !(time != null ? !time.equals(that.getTime()) : that.time != null);

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
