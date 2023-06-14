JSON Schema Generation

Features:

    1.Convert JSON to jsonSchema.
    2.Add Required field to Schema.

Maven:

    <dependency>
        <groupId>io.github.navid1981</groupId>
        <artifactId>jsonschemagenerator</artifactId>
        <version>1.0</version>
    </dependency>

Example:

    //JsonPath for required fields
    map.put(“/properties/orderDetails/properties/products/items”,” quantity,name”);
    map.put(“/properties/orderDetails/properties/billing_address/properties”,” street_1”);

    String schema= jsonToSchemaService.convertor(json,map);

Json:

{
  "count": 1,
  "testing": "update",
  "orderDetails": {
    "billing_address": {
     "term": {"name":"test"},
      "street_1": "123MainStreet",
      "testing_obj": {
        "unique": "value_unique"
      }
    },
    "products": [
      {
        "name": "VMConCloud",
        "quantity": 1,
        "term": [
          {
            "ob1": "a",
            "ob2": "a2"
          }
        ],
        "price_inc_tax": 50,
        "price_ex_tax": 45
      }
    ]
  }
}

Schema:

{
    "$schema": "http://json-schema.org/draft-07/schema#",
    "type": "object",
    "properties": {
        "count": {
            "type": "number"
        },
        "testing": {
            "type": "string"
        },
        "orderDetails": {
            "type": "object",
            "properties": {
                "billing_address": {
                    "type": "object",
                    "properties": {
                        "term": {
                            "type": "object",
                            "properties": {
                                "name": {
                                    "type": "string"
                                }
                            }
                        },
                        "street_1": {
                            "type": "string"
                        },
                        "testing_obj": {
                            "type": "object",
                            "properties": {
                                "unique": {
                                    "type": "string"
                                }
                            }
                        },
                        "required": [
                            "street_1"
                        ]
                    }
                },
                "products": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "name": {
                                "type": "string"
                            },
                            "quantity": {
                                "type": "number"
                            },
                            "term": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "ob1": {
                                            "type": "string"
                                        },
                                        "ob2": {
                                            "type": "string"
                                        }
                                    }
                                }
                            },
                            "price_inc_tax": {
                                "type": "number"
                            },
                            "price_ex_tax": {
                                "type": "number"
                            }
                        },
                        "required": [
                            "quantity",
                            "name"
                        ]
                    }
                }
            }
        }
    }
}
