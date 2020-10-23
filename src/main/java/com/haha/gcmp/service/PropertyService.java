package com.haha.gcmp.service;

import com.haha.gcmp.model.propertites.PropertyEnum;
import org.springframework.lang.NonNull;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
public interface PropertyService {
    <T> T getByPropertyOrDefault(@NonNull PropertyEnum property, @NonNull Class<T> propertyType, T defaultValue);
}
