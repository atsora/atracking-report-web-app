// Copyright (C) 2009-2023 Lemoine Automation Technologies
// Copyright (C) 2023 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reports.extensions;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.birt.data.engine.api.aggregation.Accumulator;
import org.eclipse.birt.data.engine.core.DataException;

public class LinearRegressionIntersectionAccumulator extends Accumulator {

	private int passNo = 0;
	private Map<Object, Double> sumx;
	private Map<Object, Double> sumy;
	private Map<Object, Double> sumxy;
	private Map<Object, Double> sumxx;
	private Map<Object, Integer> count;
	private Object value = null;

	private boolean isFinished = false;

	public void start() throws DataException {
		super.start();
		passNo++;

		if (passNo == 1) {
			sumx = new HashMap<Object, Double>();
			sumy = new HashMap<Object, Double>();
			sumxy = new HashMap<Object, Double>();
			sumxx = new HashMap<Object, Double>();
			count = new HashMap<Object, Integer>();

			isFinished = false;
		}
	}

	public void finish() throws DataException {
		if (passNo == 1) {
			isFinished = true;
		} else if (passNo == 2) {
			sumx = null;
			sumy = null;
			sumxy = null;
			sumxx = null;
			count = null;
		} else {
			// TODO error
		}
	}

	@Override
	public Object getValue() throws DataException {
		if (!isFinished) {
			throw new RuntimeException(
					"Error! Call summary total function before the dataset is finished");
		}
		return value;
	}

	@Override
	public void onRow(Object[] arg0) throws DataException {
		// First parameter: value
		// Second parameter: date
		// Third parameter: aggregate value

		Object group = null;
		if (arg0.length == 2) {
			group = null;
		} else if (arg0.length == 3) {
			group = arg0[2];
		} else {
			// TODO
		}
		
		if (passNo == 1) {
			Double xvalue;
			Double yvalue = (Double) arg0[0];

			if (arg0[1] instanceof Date) {
				xvalue = new Double(((Date) arg0[1]).getTime());
			} else {
				xvalue = (Double) arg0[1];
			}

			if (sumx.get(group) != null) {
				sumx.put(group, sumx.get(group) + xvalue);
			} else {
				sumx.put(group, xvalue);
			}

			if (sumy.get(group) != null) {
				sumy.put(group, sumy.get(group) + yvalue);
			} else {
				sumy.put(group, yvalue);
			}

			if (sumxy.get(group) != null) {
				sumxy.put(group, sumxy.get(group) + xvalue * yvalue);
			} else {
				sumxy.put(group, xvalue * yvalue);
			}

			if (sumxx.get(group) != null) {
				sumxx.put(group, sumxx.get(group) + xvalue * xvalue);
			} else {
				sumxx.put(group, xvalue * xvalue);
			}

			if (count.get(group) != null) {
				count.put(group, count.get(group) + 1);
			} else {
				count.put(group, 1);
			}
		} else if (passNo == 2) {
			this.value = new Double(sumy.get(group)
					/ count.get(group)
					- sumx.get(group)
					/ count.get(group)
					* (sumxy.get(group) - sumx.get(group) * sumy.get(group)
							/ count.get(group))
					/ (sumxx.get(group) - sumx.get(group) * sumx.get(group)
							/ count.get(group)));
		} else {
			// TODO error
		}
	}

}
