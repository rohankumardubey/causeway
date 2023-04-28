/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.causeway.client.kroviz.to

object VEGA_SAMPLE {
    val schema = "\$schema"

    val url = "https://vega.github.io/vega/examples/packed-bubble-chart/"
    val str = """{
  "$schema": "https://vega.github.io/schema/vega/v5.json",
  "width": 800,
  "height": 500,
  "padding": {"left": 5, "right": 5, "top": 20, "bottom": 0},
  "autosize": "none",
  "signals": [
    {"name": "cx", "update": "width / 2"},
    {"name": "cy", "update": "height / 2"},
    {
      "name": "gravityX",
      "value": 0.2,
      "bind": {"input": "range", "min": 0, "max": 1}
    },
    {
      "name": "gravityY",
      "value": 0.1,
      "bind": {"input": "range", "min": 0, "max": 1}
    }
  ],
  "data": [
    {
      "name": "table",
      "values": [
        {"category": "A", "amount": 0.28},
        {"category": "B", "amount": 0.55},
        {"category": "C", "amount": 0.43},
        {"category": "D", "amount": 0.91},
        {"category": "E", "amount": 0.81},
        {"category": "F", "amount": 0.53},
        {"category": "G", "amount": 0.19},
        {"category": "H", "amount": 0.87},
        {"category": "I", "amount": 0.28},
        {"category": "J", "amount": 0.55},
        {"category": "K", "amount": 0.43},
        {"category": "L", "amount": 0.91},
        {"category": "M", "amount": 0.81},
        {"category": "N", "amount": 0.53},
        {"category": "O", "amount": 0.19},
        {"category": "P", "amount": 0.87}
      ]
    }
  ],
  "scales": [
    {
      "name": "size",
      "domain": {"data": "table", "field": "amount"},
      "range": [100, 3000]
    },
    {
      "name": "color",
      "type": "ordinal",
      "domain": {"data": "table", "field": "category"},
      "range": "ramp"
    }
  ],
  "marks": [
    {
      "name": "nodes",
      "type": "symbol",
      "from": {"data": "table"},
      "encode": {
        "enter": {
          "fill": {"scale": "color", "field": "category"},
          "xfocus": {"signal": "cx"},
          "yfocus": {"signal": "cy"}
        },
        "update": {
          "size": {"signal": "pow(2 * datum.amount, 2)", "scale": "size"},
          "stroke": {"value": "white"},
          "strokeWidth": {"value": 1},
          "tooltip": {"signal": "datum"}
        }
      },
      "transform": [
        {
          "type": "force",
          "iterations": 100,
          "static": false,
          "forces": [
            {
              "force": "collide",
              "iterations": 2,
              "radius": {"expr": "sqrt(datum.size) / 2"}
            },
            {"force": "center", "x": {"signal": "cx"}, "y": {"signal": "cy"}},
            {"force": "x", "x": "xfocus", "strength": {"signal": "gravityX"}},
            {"force": "y", "y": "yfocus", "strength": {"signal": "gravityY"}}
          ]
        }
      ]
    },
    {
      "type": "text",
      "from": {"data": "nodes"},
      "encode": {
        "enter": {
          "align": {"value": "center"},
          "baseline": {"value": "middle"},
          "fontSize": {"value": 15},
          "fontWeight": {"value": "bold"},
          "fill": {"value": "white"},
          "text": {"field": "datum.category"}
        },
        "update": {"x": {"field": "x"}, "y": {"field": "y"}}
      }
    }
  ]
}

"""
}
