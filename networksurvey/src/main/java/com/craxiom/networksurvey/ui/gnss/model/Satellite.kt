/*
 * Copyright (C) 2019 Sean J. Barbeau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.craxiom.networksurvey.ui.gnss.model

import com.craxiom.networksurvey.model.SatelliteStatus

/**
 * A container class that represents a satellite sending GNSS or SBAS signals ([status]). The [id] of
 * each satellite is a composite key of the constellation (e.g., GPS) and satellite ID (e.g., 11)
 * using GPSTestUtil.createGnssSatelliteKey().
 */
data class Satellite(
    val id: String,
    // Individual signals are stored in a map with the carrier frequency label as the key so we can
    // see if there are duplicate frequencies.
    val status: Map<String, SatelliteStatus>
)
