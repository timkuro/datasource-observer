/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.dwd;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;

import com.esotericsoftware.minlog.Log;

import de.wacodis.dwd.cdc.DwdProductsMetadata;
import de.wacodis.dwd.cdc.DwdProductsMetadataDecoder;
import de.wacodis.dwd.cdc.DwdRequestParamsEncoder;
import de.wacodis.dwd.cdc.DwdWfsRequestParams;
import de.wacodis.dwd.cdc.DwdWfsRequestor;
import de.wacodis.observer.model.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.observer.model.DwdDataEnvelope;
import de.wacodis.observer.model.WacodisJobDefinitionTemporalCoverage;
import de.wacodis.observer.publisher.PublisherChannel;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class DwdJob implements Job {

	public static String LAYER_NAME_KEY = "layerName";

	private static final Logger LOG = LoggerFactory.getLogger(DwdJob.class);

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		LOG.info("Start DwdJob's execute()");
		JobDataMap dataMap = jec.getJobDetail().getJobDataMap();
		// TODO
		// 1) Get all required request parameters stored in the JobDataMap
		String layerName = dataMap.getString(LAYER_NAME_KEY);
		String serviceUrl = dataMap.getString("serviceUrl");
		WacodisJobDefinitionTemporalCoverage executionTemporalCoverage = (WacodisJobDefinitionTemporalCoverage) dataMap
				.get("executionTemporalCoverage");

		AbstractDataEnvelopeAreaOfInterest executionArea = (AbstractDataEnvelopeAreaOfInterest) dataMap
				.get("executionArea");
		List<Float> area = executionArea.getExtent();

		// 2) Create a DwdWfsRequestParams onbject from the restored request parameters
		DwdWfsRequestParams params = DwdRequestParamsEncoder.encode(version, layerName, area, startDate, endDate);

		// - startDate and endDate should be chosen depending on the request interval
		// and the last request endDate
		// 3) Request WFS with request paramaters
		DwdProductsMetadata metadata = DwdWfsRequestor.request(serviceUrl, params);
		// 4) Decode DwdProductsMetada to DwdDataEnvelope
		DwdDataEnvelope dataEnvelope = DwdProductsMetadataDecoder.decode(metadata);
		LOG.info("new dataEnvelope:\n{}", dataEnvelope.toString());
		// 5) Publish DwdDataEnvelope message
		PublisherChannel pub;
		pub.sendDataEnvelope().send(MessageBuilder.withPayload(dataEnvelope).build());
		LOG.info("DataEnvelope published");
	}

}
