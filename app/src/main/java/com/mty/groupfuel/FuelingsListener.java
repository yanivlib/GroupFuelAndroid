package com.mty.groupfuel;

import com.mty.groupfuel.datamodel.Fueling;

import java.util.List;

public interface FuelingsListener {
    List<Fueling> getFuelings();

    void setFuelings(List<Fueling> fuelings);

    void addFueling(Fueling fueling);

    void syncFueling();
}
