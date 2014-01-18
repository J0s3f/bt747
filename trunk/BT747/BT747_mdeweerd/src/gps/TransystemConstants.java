/*
 * Copyright 2011 Mario De Weerd.
 */
package gps;

public final class TransystemConstants {

	/* Query the device type. $PTSI000,TSI*4C */
	public static final String PTSI_CMD_Q_DEVICETYPE_STR = "000";
	public static final int PTSI_CMD_Q_DEVICETYPE = 0;
	/* Response with the device type.  Example: $PTSI001,QST1000P*04. */
	public static final String PTSI_CMD_D_DEVICETYPE_STR = "001";
	public static int PTSI_CMD_D_DEVICETYPE = 1;
	/* Query ... $PTSI002,2*32 */
	/* $PTSI002,1, *3D */
	/* $PTSI003,0*31 */
	/* $PTSI004,0*36 */
	/* $PTSI004,2,0*28 */
	/* $PTSI012,0*31 */
	/* $PTSI013,0*30 */
	/* $PTSI014*2B */
	/* $PTSI015*2A */
	/* $PTSI020,1,0,0*31 */
	
}
