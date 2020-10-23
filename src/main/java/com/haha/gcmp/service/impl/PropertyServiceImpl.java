package com.haha.gcmp.service.impl;

import com.haha.gcmp.model.propertites.PropertyEnum;
import com.haha.gcmp.service.PropertyService;
import org.springframework.stereotype.Service;

/**
 * @author SZFHH
 * @date 2020/10/23
 */
@Service
public class PropertyServiceImpl implements PropertyService {
    @Override
    public <T> T getByPropertyOrDefault(PropertyEnum property, Class<T> propertyType, T defaultValue) {
        return (T) Boolean.valueOf(true);
    }


}
