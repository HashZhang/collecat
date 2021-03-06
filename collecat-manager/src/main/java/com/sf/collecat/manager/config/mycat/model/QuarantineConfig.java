/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package com.sf.collecat.manager.config.mycat.model;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;

import java.util.List;
import java.util.Map;

/**
 * 隔离区配置定义
 *
 * @author songwie
 */
public final class QuarantineConfig {

    private Map<String, List<UserConfig>> whitehost;
    private List<String> blacklist;
    private boolean check = false;

    private WallConfig wallConfig = new WallConfig();

    private static WallProvider provider;

    public QuarantineConfig() {
    }

    public void init() {
        if (check) {
            provider = new MySqlWallProvider(wallConfig);
            provider.setBlackListEnable(true);
        }
    }

    public WallProvider getWallProvider() {
        return provider;
    }

    public Map<String, List<UserConfig>> getWhitehost() {
        return this.whitehost;
    }

    public void setWhitehost(Map<String, List<UserConfig>> whitehost) {
        this.whitehost = whitehost;
    }

    public boolean addWhitehost(String host, List<UserConfig> Users) {
        if (existsHost(host)) {
            return false;
        } else {
            this.whitehost.put(host, Users);
            return true;
        }
    }

    public List<String> getBlacklist() {
        return this.blacklist;
    }

    public void setBlacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }

    public WallProvider getProvider() {
        return provider;
    }

    public boolean existsHost(String host) {
        return this.whitehost == null ? false : whitehost.get(host) != null;
    }


    public static void setProvider(WallProvider provider) {
        QuarantineConfig.provider = provider;
    }

    public void setWallConfig(WallConfig wallConfig) {
        this.wallConfig = wallConfig;

    }

    public boolean isCheck() {
        return this.check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public WallConfig getWallConfig() {
        return this.wallConfig;
    }





}