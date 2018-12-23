package com.sap.cloud.s4hana.examples.addressmgr.commands;

import com.sap.cloud.sdk.frameworks.hystrix.HystrixUtil;
import com.sap.cloud.sdk.s4hana.connectivity.ErpCommand;
import org.slf4j.Logger;

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartnerAddress;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BusinessPartnerService;

@SuppressWarnings("PMD")
public class CreateAddressCommand extends ErpCommand<BusinessPartnerAddress> {
    private static final Logger logger = CloudLoggerFactory.getLogger(CreateAddressCommand.class);

    private final BusinessPartnerService service;
    private final BusinessPartnerAddress addressToCreate;

    public CreateAddressCommand(final BusinessPartnerService service, final BusinessPartnerAddress addressToCreate) {
        /* by doing this all instances of our class will run with the same hystrix command key and s4hana cloud sdk
        will apply a default configuration for our command */
        //super(CreateAddressCommand.class);

        //but we can also change the default behavior - adding resilient time out behavior of 10 seconds
        super(HystrixUtil.getDefaultErpCommandSetter(
                CreateAddressCommand.class,
                HystrixUtil.getDefaultErpCommandProperties().withExecutionTimeoutInMilliseconds(10000)));

        this.service = service;
        this.addressToCreate = addressToCreate;
    }

    @Override
    protected BusinessPartnerAddress run() throws Exception {
        return service.createBusinessPartnerAddress(addressToCreate).execute();
    }
}
