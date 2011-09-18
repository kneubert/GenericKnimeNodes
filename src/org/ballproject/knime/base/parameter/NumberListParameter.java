/*
 * Copyright (c) 2011, Marc Röttig.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ballproject.knime.base.parameter;

import java.util.List;

public abstract class NumberListParameter<T> extends Parameter<List<T>>
{

	protected T lowerBound;
	protected T upperBound;
	
	public NumberListParameter(String key, List<T> value)
	{
		super(key, value);
	}

	public T getLowerBound()
	{
		return lowerBound;
	}

	public void setLowerBound(T lowerBound)
	{
		this.lowerBound = lowerBound;
	}

	public T getUpperBound()
	{
		return upperBound;
	}

	public void setUpperBound(T upperBound)
	{
		this.upperBound = upperBound;
	}
}
