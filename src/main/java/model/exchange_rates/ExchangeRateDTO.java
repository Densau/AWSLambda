package model.exchange_rates;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.uuid.Generators;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@DynamoDBTable(tableName = "currencyExchange")
public class ExchangeRateDTO {

	@DynamoDBAutoGeneratedKey
	@DynamoDBHashKey(attributeName = "exchangeDate")
	private String exchangeDate;

	@DynamoDBRangeKey(attributeName = "uuidTimestamp")
	private String uuidTimestamp;

	@DynamoDBRangeKey(attributeName = "exchangeTimestamp")
	private long exchangeTimestamp;

	@DynamoDBRangeKey(attributeName = "exchangeString")
	private String exchangeString;

	public ExchangeRateDTO() {
	}

	public ExchangeRateDTO(String exchangeDate, String uuidTimestamp, long exchangeTimestamp, String exchangeString) {
		this.exchangeDate      = exchangeDate;
		this.uuidTimestamp     = uuidTimestamp;
		this.exchangeTimestamp = exchangeTimestamp;
		this.exchangeString    = exchangeString;
	}

	public ExchangeRateDTO(long timestamp, String exchangeString) {
		this.uuidTimestamp  = Generators.timeBasedGenerator().generate().toString();
		this.exchangeString = exchangeString;
		LocalDateTime exchangeTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC);
		this.exchangeDate      = String.format("%d/%d/%d", exchangeTime.getYear(), exchangeTime.getMonthValue(),
		                                       exchangeTime.getDayOfMonth());
		this.exchangeTimestamp = timestamp;

	}

	public String getExchangeDate() {
		return exchangeDate;
	}

	public ExchangeRateDTO setExchangeDate(String exchangeDate) {
		this.exchangeDate = exchangeDate;
		return this;
	}

	public String getUuidTimestamp() {
		return uuidTimestamp;
	}

	public ExchangeRateDTO setUuidTimestamp(String uuidTimestamp) {
		this.uuidTimestamp = uuidTimestamp;
		return this;
	}

	public long getExchangeTimestamp() {
		return exchangeTimestamp;
	}

	public ExchangeRateDTO setExchangeTimestamp(long exchangeTimestamp) {
		this.exchangeTimestamp = exchangeTimestamp;
		return this;
	}

	public String getExchangeString() {
		return exchangeString;
	}

	public ExchangeRateDTO setExchangeString(String exchangeString) {
		this.exchangeString = exchangeString;
		return this;
	}
}
