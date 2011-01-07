/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.revision.core;

import de.topicmapslab.majortom.model.core.IVariant;

/**
 * @author Sven Krosse
 * 
 */
public abstract class ReadOnlyVariant extends ReadOnlyDatatypeAware implements IVariant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 886824371721326354L;

	/**
	 * @param clone
	 */
	public ReadOnlyVariant(IVariant clone) {
		super(clone);
	}

}
