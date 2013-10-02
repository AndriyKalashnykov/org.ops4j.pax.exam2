/*
 * Copyright 2009 Toni Menzel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.invoker.junit.internal;


public class MyNotSerializableException extends RuntimeException {
    private static final long serialVersionUID = 6429496713575239757L;
    private NotSerializablePart detail;
    
    public MyNotSerializableException(String message) {
        super(message);
        this.detail = new NotSerializablePart();
    }

    public NotSerializablePart getDetail() {
        return detail;
    }

    public class NotSerializablePart {
    }
    
}
