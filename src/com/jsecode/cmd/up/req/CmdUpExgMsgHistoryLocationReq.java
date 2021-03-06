/**
 * @author 	Jadic
 * @created 2014-2-17
 */
package com.jsecode.cmd.up.req;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;

import com.jsecode.cmd.CmdHeadSubBizWithCar;
import com.jsecode.cmd.bean.GpsCmdBean;

/**
 * 车辆定位信息自动补报请求消息
 * 
 * 描述：如果平台间传输链路中断，下级平台重新登录并与上级平台建立通信链路后，
 * 下级平台应将中断期间内车载终端上传的车辆定位信息自动补报到上级平台。如果
 * 系统断线期间，该车需发送的数据包条数大于5，则以每包五条进行补发，直到补
 * 发完毕。多条数据以卫星定位时间先后顺序排列。本条消息上级平台采用定量回复
 * ，即收到一定数量的数据后，即通过从链路应答数据量
 * @author Jadic
 */
public class CmdUpExgMsgHistoryLocationReq extends CmdHeadSubBizWithCar {
	
	private byte gpsCount;
	private List<GpsCmdBean> gpsList;

	public CmdUpExgMsgHistoryLocationReq() {
		gpsList = GPS_LIST_EMPTY;
	}

	@Override
	protected int getCmdSubBizDataSize() {
		if (gpsList.size() > 0) {
			return 1 + gpsList.size() * gpsList.get(0).getBeanSize();
		} else {
			return 1;
		}
	}

	@Override
	protected void disposeCmdSubBizData(ChannelBuffer channelBuffer) {
		this.gpsCount = channelBuffer.readByte();
		gpsList = new ArrayList<GpsCmdBean>();
		for (int i = 0; i < this.gpsCount; i ++) {
			GpsCmdBean gpsCmdBean = new GpsCmdBean();
			gpsCmdBean.disposeData(channelBuffer);
			gpsList.add(gpsCmdBean);
		}
	}

	@Override
	protected void fillCmdSubBizData(ChannelBuffer channelBuffer) {
		channelBuffer.writeByte(this.gpsCount);
		for (GpsCmdBean gpsBean : gpsList) {
			gpsBean.fillChannelBuffer(channelBuffer);
		}
	}

	@Override
	public String getDBSaveContent() {
		return EMPTY_STR;
	}
	
	public void addGpsData(GpsCmdBean gpsBean) {
		if (gpsBean != null) {
			this.gpsList.add(gpsBean);
			this.gpsCount = (byte)this.gpsList.size();
		}
	}

	public byte getGpsCount() {
		return gpsCount;
	}

}
