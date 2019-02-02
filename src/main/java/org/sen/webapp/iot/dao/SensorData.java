package org.sen.webapp.iot.dao;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class SensorData {
    @Id Long id;
    @Index
    String deviceId;
    @Index
    long timestamp;
    @Index
    String temperature;
    @Index
    String humidity;
    @Index
    String pH;
    @Index
    String conductivity;
	/**
	 * @return the id
	 */
	public Long getId()
		{
			return id;
		}
	/**
	 * @param id the id to set
	 */
	public void setId( Long id )
		{
			this.id = id;
		}
	/**
	 * @return the deviceId
	 */
	public String getDeviceId()
		{
			return deviceId;
		}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId( String deviceId )
		{
			this.deviceId = deviceId;
		}
	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
		{
			return timestamp;
		}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp( long timestamp )
		{
			this.timestamp = timestamp;
		}
	/**
	 * @return the temperature
	 */
	public String getTemperature()
		{
			return temperature;
		}
	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature( String temperature )
		{
			this.temperature = temperature;
		}
	/**
	 * @return the humidity
	 */
	public String getHumidity()
		{
			return humidity;
		}
	/**
	 * @param humidity the humidity to set
	 */
	public void setHumidity( String humidity )
		{
			this.humidity = humidity;
		}
	/**
	 * @return the pH
	 */
	public String getpH()
		{
			return pH;
		}
	/**
	 * @param pH the pH to set
	 */
	public void setpH( String pH )
		{
			this.pH = pH;
		}
	/**
	 * @return the conductivity
	 */
	public String getConductivity()
		{
			return conductivity;
		}
	/**
	 * @param conductivity the conductivity to set
	 */
	public void setConductivity( String conductivity )
		{
			this.conductivity = conductivity;
		}
}