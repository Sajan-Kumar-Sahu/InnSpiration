package com.backbenchcoders.innspiration.strategy;

import com.backbenchcoders.innspiration.entity.Inventory;

import java.math.BigDecimal;


public class BasePricingStrategy implements PricingStrategy{

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();

    }
}
