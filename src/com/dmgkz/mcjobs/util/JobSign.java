/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmgkz.mcjobs.util;

/**
 *
 * @author Bl4ckSkull666
 */
public class JobSign {
    private final SignType _siType;
    private final String _job;
    public JobSign(String job, SignType siType) {
        _job = job;
        _siType = siType;
    }
       
    public SignType getSignType() {
        return _siType;
    }
      
    public String getJob() {
        return _job;
    }
}