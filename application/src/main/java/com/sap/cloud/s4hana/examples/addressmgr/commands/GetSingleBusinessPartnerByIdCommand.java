package com.sap.cloud.s4hana.examples.addressmgr.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.s4hana.connectivity.CachingErpCommand;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("PMD")
public class GetSingleBusinessPartnerByIdCommand extends CachingErpCommand<BusinessPartner> {
    private static final Logger logger = CloudLoggerFactory.getLogger(GetSingleBusinessPartnerByIdCommand.class);

    private final BusinessPartnerService service;
    private final String id;

    private static final Cache<CacheKey, BusinessPartner> cache =
            CacheBuilder.newBuilder()
                    .maximumSize(50)
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .build();

    public GetSingleBusinessPartnerByIdCommand(final BusinessPartnerService service, final String id) {

        super(GetAllBusinessPartnersCommand.class);
        this.service = service;
        this.id = id;
    }

    @Override
    protected BusinessPartner runCacheable() throws Exception {
        return service.getBusinessPartnerByKey(id)
                      .select(BusinessPartner.BUSINESS_PARTNER,
                              BusinessPartner.LAST_NAME,
                              BusinessPartner.FIRST_NAME,
                              BusinessPartner.IS_MALE,
                              BusinessPartner.IS_FEMALE,
                              BusinessPartner.CREATION_DATE,
                              BusinessPartner.TO_BUSINESS_PARTNER_ADDRESS.select(
                                      BusinessPartnerAddress.BUSINESS_PARTNER,
                                      BusinessPartnerAddress.ADDRESS_ID,
                                      BusinessPartnerAddress.COUNTRY,
                                      BusinessPartnerAddress.POSTAL_CODE,
                                      BusinessPartnerAddress.CITY_NAME,
                                      BusinessPartnerAddress.STREET_NAME,
                                      BusinessPartnerAddress.HOUSE_NUMBER
                              ))
                      .execute();
    }

    @Nonnull
    @Override
    protected Cache<CacheKey, BusinessPartner> getCache() {
        return cache;
    }

    @Nonnull
    @Override
    protected CacheKey getCommandCacheKey() {
        return super.getCommandCacheKey().append(id);
    }

    @Override
    protected BusinessPartner getFallback() {
        logger.warn("Fallback called because of exception:", getExecutionException());
        return BusinessPartner.builder().businessPartner(id).build();
    }

}
