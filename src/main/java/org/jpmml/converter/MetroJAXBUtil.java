/*
 * Copyright (c) 2015 Villu Ruusmann
 *
 * This file is part of JPMML-Converter
 *
 * JPMML-Converter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-Converter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-Converter.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jpmml.converter;

import java.io.OutputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.sun.xml.bind.marshaller.MinimumEscapeHandler;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.MarshallerImpl;
import org.dmg.pmml.PMML;
import org.jpmml.model.JAXBUtil;

public class MetroJAXBUtil {

	private MetroJAXBUtil(){
	}

	static
	public void marshalPMML(PMML pmml, OutputStream os) throws JAXBException {
		Marshaller marshaller = JAXBUtil.createMarshaller();

		if(marshaller instanceof MarshallerImpl){
			MarshallerImpl marshallerImpl = (MarshallerImpl)marshaller;
			JAXBContextImpl contextImpl = (JAXBContextImpl)marshallerImpl.getContext();

			marshallerImpl.marshal(pmml, new PrettyUTF8XmlOutput(os, contextImpl.getUTF8NameTable(), MinimumEscapeHandler.theInstance));
		} else

		{
			marshaller.marshal(pmml, os);
		}
	}
}