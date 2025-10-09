package com.solutec.business_parameter_service.service;

import com.solutec.business_parameter_service.entity.*;
import com.solutec.business_parameter_service.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class BusinessParameterService {

    @Autowired
    private BusinessParameterRepository parameterRepository;

    @Autowired
    private ParameterHistoryRepository historyRepository;

    @Cacheable(value = "businessParameters", key = "#name")
    public String getParameterValue(String name) {
        log.debug("Fetching parameter value for: {}", name);
        return parameterRepository.findByNameAndIsActiveTrue(name)
                .map(BusinessParameter::getValue)
                .orElse(null);
    }

    public BigDecimal getParameterAsDecimal(String name, BigDecimal defaultValue) {
        try {
            String value = getParameterValue(name);
            return value != null ? new BigDecimal(value) : defaultValue;
        } catch (Exception e) {
            log.error("Error parsing parameter {} as decimal", name, e);
            return defaultValue;
        }
    }

    public Integer getParameterAsInteger(String name, Integer defaultValue) {
        try {
            String value = getParameterValue(name);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (Exception e) {
            log.error("Error parsing parameter {} as integer", name, e);
            return defaultValue;
        }
    }

    public Boolean getParameterAsBoolean(String name, Boolean defaultValue) {
        try {
            String value = getParameterValue(name);
            return value != null ? Boolean.parseBoolean(value) : defaultValue;
        } catch (Exception e) {
            log.error("Error parsing parameter {} as boolean", name, e);
            return defaultValue;
        }
    }

    public LocalDate getParameterAsDate(String name, LocalDate defaultValue) {
        try {
            String value = getParameterValue(name);
            return value != null ? LocalDate.parse(value) : defaultValue;
        } catch (Exception e) {
            log.error("Error parsing parameter {} as date", name, e);
            return defaultValue;
        }
    }

    public List<BusinessParameter> getAllActive() {
        return parameterRepository.findByIsActiveTrue();
    }

    @Cacheable(value = "parametersByCategory", key = "#category")
    public List<BusinessParameter> getByCategory(String category) {
        return parameterRepository.findByCategoryAndIsActiveTrue(category);
    }

    public List<String> getAllCategories() {
        return parameterRepository.findAllActiveCategories();
    }

    public Optional<BusinessParameter> getById(Integer id) {
        return parameterRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = {"businessParameters", "parametersByCategory"}, allEntries = true)
    public BusinessParameter updateParameter(Integer id, String value, String changedBy, String reason) {
        Optional<BusinessParameter> paramOpt = parameterRepository.findById(id);
        if (paramOpt.isEmpty()) {
            throw new RuntimeException("Parameter not found with id: " + id);
        }

        BusinessParameter param = paramOpt.get();
        String oldValue = param.getValue();

        // Guardar en historial
        ParameterHistory history = new ParameterHistory();
        history.setParameterId(param.getParameterId());
        history.setParameterName(param.getName());
        history.setOldValue(oldValue);
        history.setNewValue(value);
        history.setChangedBy(changedBy);
        history.setAction("UPDATE");
        historyRepository.save(history);

        // Actualizar parámetro
        param.setValue(value);
        param.setChangedBy(changedBy);

        BusinessParameter updated = parameterRepository.save(param);
        log.info("Parameter {} updated from {} to {} by {}",
                param.getName(), oldValue, value, changedBy);

        return updated;
    }

    @Transactional
    @CacheEvict(value = {"businessParameters", "parametersByCategory"}, allEntries = true)
    public BusinessParameter createParameter(BusinessParameter parameter, String changedBy) {
        if (parameterRepository.existsByName(parameter.getName())) {
            throw new RuntimeException("Parameter with name " + parameter.getName() + " already exists");
        }

        parameter.setChangedBy(changedBy);
        BusinessParameter created = parameterRepository.save(parameter);

        // Guardar en historial
        ParameterHistory history = new ParameterHistory();
        history.setParameterId(created.getParameterId());
        history.setParameterName(created.getName());
        history.setNewValue(created.getValue());
        history.setChangedBy(changedBy);
        history.setAction("CREATE");
        historyRepository.save(history);

        log.info("Parameter {} created with value {} by {}",
                created.getName(), created.getValue(), changedBy);

        return created;
    }

    @Transactional
    @CacheEvict(value = {"businessParameters", "parametersByCategory"}, allEntries = true)
    public void deleteParameter(Integer id, String changedBy) {
        Optional<BusinessParameter> paramOpt = parameterRepository.findById(id);
        if (paramOpt.isEmpty()) {
            throw new RuntimeException("Parameter not found with id: " + id);
        }

        BusinessParameter param = paramOpt.get();
        param.setIsActive(false);
        param.setChangedBy(changedBy);
        parameterRepository.save(param);

        // Guardar en historial
        ParameterHistory history = new ParameterHistory();
        history.setParameterId(param.getParameterId());
        history.setParameterName(param.getName());
        history.setOldValue(param.getValue());
        history.setChangedBy(changedBy);
        history.setAction("DELETE");
        historyRepository.save(history);

        log.info("Parameter {} deactivated by {}", param.getName(), changedBy);
    }

    public List<ParameterHistory> getParameterHistory(Integer parameterId) {
        return historyRepository.findByParameterIdOrderByChangedAtDesc(parameterId);
    }

    public List<ParameterHistory> getRecentChanges() {
        return historyRepository.findTop10ByOrderByChangedAtDesc();
    }
}