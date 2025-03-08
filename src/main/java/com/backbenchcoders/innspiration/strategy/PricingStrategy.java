package com.backbenchcoders.innspiration.strategy;

import com.backbenchcoders.innspiration.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
