/*******************************************************************************
**  Copyright: Acsia Technologies PVT LTD                                     **
********************************************************************************
**  Filename: IAudioService.aidl                                            **
**  Project: Panasonic POC                                                    **
**  Language:   Java                                                          **
**  Description: Java interface file for AudioHandler                         **
********************************************************************************
** Version    Date         Author     Modification                            **
** 1.0     07-DEC-2016    Abdulla     - First edition                         **
*******************************************************************************/

package com.acsia.server;

/**
 * This is the service for handling functions related to volume settings
 * Supported functions are
 * 1) get max volume of audio device
 * 2) get current volume of audio device
 * 3) set volume to specified volumeLevel of audio device
 * 4) mute/unmute audio device
 */
interface IAudioService {
    /**
     * *****************************************************************************
     * *	FunctionName: getMaxAudioLevel		                     		  		  **
     * *	Parameters: 															  **
     * *	Return: 																  **
     * *		maximum volume level	              							      **
     * *	Description: Gets the max volume level of audio device                    **
     * ******************************************************************************
     */
    int getMaxAudioLevel();

    /**
     * *****************************************************************************
     * *	FunctionName: getAudioLevel		         		           		  		  **
     * *	Parameters: 															  **
     * *	Return: 																  **
     * *		current volume level	              							      **
     * *	Description: Gets the current volume level of audio device                **
     * ******************************************************************************
     */
    int getAudioLevel();

    /**
     * *****************************************************************************
     * *	FunctionName: setVolume		                     		  				  **
     * *	Parameters: 															  **
     * *		volumeLevel(volume level) 				  				              **
     * *		headUnit(true if it is head unit otherwise false) 			          **
     * *	Return: 																  **
     * *		boolean value representing the success or failure	                  **
     * *	Description: Sets the volume level of audio device to specified level     **
     * ******************************************************************************
     *
     * @param volumeLevel
     * @param headUnit
     */
    boolean setAudioLevel(int volumeLevel, boolean headUnit);

    /*******************************************************************************
    **	FunctionName: isMute		                   		  					  **
    **	Parameters: 															  **
    **	Return: 																  **
    **		boolean value representing audio device mute   		                  **
    **	Description: boolean value representing muteness of audio device		  **
    ********************************************************************************/
     boolean isMute();

    /**
     * *****************************************************************************
     * *	FunctionName: muteAudio		                   		  					  **
     * *	Parameters: 															  **
     * *		true or false value to mute or unmute audio device 				  	  **
     * *	Return: 																  **
     * *		boolean value representing the success or failure	                  **
     * *	Description: Mute or Unmute volume of target audio device depending on    **
     * *				 the value of parameter mute   								  **
     * ******************************************************************************
     *
     * @param mute
     */
    boolean muteAudio(boolean mute);

}
