/*
 * Copyright (c) 2016 Villu Ruusmann
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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Entity;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.Output;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.ResultFeature;
import org.dmg.pmml.Target;

public class ModelUtil {

	private ModelUtil(){
	}

	static
	public MiningSchema createMiningSchema(Schema schema){
		return createMiningSchema(schema.getLabel());
	}

	static
	public MiningSchema createMiningSchema(Label label){
		MiningSchema miningSchema = new MiningSchema();

		if(label != null){
			FieldName name = label.getName();

			if(name != null){
				MiningField miningField = createMiningField(name, MiningField.UsageType.TARGET);

				miningSchema.addMiningFields(miningField);
			}
		}

		return miningSchema;
	}

	static
	public MiningField createMiningField(FieldName name){
		return createMiningField(name, null);
	}

	static
	public MiningField createMiningField(FieldName name, MiningField.UsageType usageType){
		MiningField miningField = new MiningField(name)
			.setUsageType(usageType);

		return miningField;
	}

	static
	public Target createRescaleTarget(Schema schema, Double slope, Double intercept){
		return createRescaleTarget(schema.getLabel(), slope, intercept);
	}

	static
	public Target createRescaleTarget(Label label, Double slope, Double intercept){
		FieldName name = label.getName();

		Target target = new Target()
			.setField(name);

		if(slope != null && !ValueUtil.isOne(slope)){
			target.setRescaleFactor(slope);
		} // End if

		if(intercept != null && !ValueUtil.isZero(intercept)){
			target.setRescaleConstant(intercept);
		}

		return target;
	}

	static
	public Output createPredictedOutput(FieldName name, OpType opType, DataType dataType, Transformation... transformations){
		List<OutputField> outputFields = new ArrayList<>();

		OutputField outputField = new OutputField(name, dataType)
			.setOpType(opType)
			.setResultFeature(ResultFeature.PREDICTED_VALUE)
			.setFinalResult(false);

		outputFields.add(outputField);

		for(Transformation transformation : transformations){
			outputField = new OutputField(transformation.getName(outputField.getName()), transformation.getDataType(outputField.getDataType()))
				.setOpType(transformation.getOpType(outputField.getOpType()))
				.setResultFeature(ResultFeature.TRANSFORMED_VALUE)
				.setFinalResult(transformation.isFinalResult())
				.setExpression(transformation.createExpression(new FieldRef(outputField.getName())));

			outputFields.add(outputField);
		}

		Output output = new Output(outputFields);

		return output;
	}

	static
	public Output createProbabilityOutput(Schema schema){
		return createProbabilityOutput((CategoricalLabel)schema.getLabel());
	}

	static
	public Output createProbabilityOutput(CategoricalLabel label){
		List<String> values = label.getValues();

		Output output = new Output(createProbabilityFields(values));

		return output;
	}

	static
	public OutputField createAffinityField(String value){
		return createAffinityField(FieldName.create("affinity_" + value), value);
	}

	static
	public OutputField createAffinityField(FieldName name, String value){
		OutputField outputField = new OutputField(name, DataType.DOUBLE)
			.setOpType(OpType.CONTINUOUS)
			.setResultFeature(ResultFeature.AFFINITY)
			.setValue(value);

		return outputField;
	}

	static
	public List<OutputField> createAffinityFields(List<? extends Entity> entities){
		Function<Entity, OutputField> function = new Function<Entity, OutputField>(){

			@Override
			public OutputField apply(Entity entity){
				return createAffinityField(entity.getId());
			}
		};

		return Lists.newArrayList(Lists.transform(entities, function));
	}

	static
	public OutputField createEntityIdField(FieldName name){
		OutputField outputField = new OutputField(name, DataType.STRING)
			.setOpType(OpType.CATEGORICAL)
			.setResultFeature(ResultFeature.ENTITY_ID);

		return outputField;
	}

	static
	public OutputField createPredictedField(FieldName name, DataType dataType, OpType opType){
		OutputField outputField = new OutputField(name, dataType)
			.setOpType(opType)
			.setResultFeature(ResultFeature.PREDICTED_VALUE);

		return outputField;
	}

	static
	public OutputField createProbabilityField(String value){
		return createProbabilityField(FieldName.create("probability_" + value), value);
	}

	static
	public OutputField createProbabilityField(FieldName name, String value){
		OutputField outputField = new OutputField(name, DataType.DOUBLE)
			.setOpType(OpType.CONTINUOUS)
			.setResultFeature(ResultFeature.PROBABILITY)
			.setValue(value);

		return outputField;
	}

	static
	public List<OutputField> createProbabilityFields(List<String> values){
		Function<String, OutputField> function = new Function<String, OutputField>(){

			@Override
			public OutputField apply(String value){
				return createProbabilityField(value);
			}
		};

		return Lists.newArrayList(Lists.transform(values, function));
	}
}