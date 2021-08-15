package com.example.compass.utils;

import com.example.compass.models.DestinationInputStatus;

import org.junit.Assert;
import org.junit.Test;

public class DestinationInputValidatorTest {

    @Test
    public void validateDestinationInput_bothFieldsEmpty_statusEmpty() {
        Assert.assertEquals(
                DestinationInputStatus.EMPTY,
                DestinationInputValidator.validateDestinationInput(
                        "",
                        ""
                )
        );
    }

    @Test
    public void validateDestinationInput_latFieldEmpty_statusEmpty() {
        Assert.assertEquals(
                DestinationInputStatus.EMPTY,
                DestinationInputValidator.validateDestinationInput(
                        "",
                        "5.89"
                )
        );
    }

    @Test
    public void validateDestinationInput_lonFieldEmpty_statusEmpty() {
        Assert.assertEquals(
                DestinationInputStatus.EMPTY,
                DestinationInputValidator.validateDestinationInput(
                        "-80.2345",
                        ""
                )
        );
    }

    @Test
    public void validationDestinationInput_latIsTooSmall_statusOutOfRange() {
        Assert.assertEquals(
                DestinationInputStatus.OUT_OF_RANGE,
                DestinationInputValidator.validateDestinationInput(
                        "-90.2345",
                        "59.004"
                )
        );
    }

    @Test
    public  void validationDestinationInput_latIsTooBig_statusOutOfRange() {
        Assert.assertEquals(
                DestinationInputStatus.OUT_OF_RANGE,
                DestinationInputValidator.validateDestinationInput(
                        "90.2345",
                        "59.004"
                )
        );
    }

    @Test
    public void validationDestinationInput_lonIsTooSmall_statusOutOfRange() {
        Assert.assertEquals(
                DestinationInputStatus.OUT_OF_RANGE,
                DestinationInputValidator.validateDestinationInput(
                        "-30.2345",
                        "-180.2344"
                )
        );
    }

    @Test
    public void validationDestinationInput_lonIsTooBig_statusOutOfRange() {
        Assert.assertEquals(
                DestinationInputStatus.OUT_OF_RANGE,
                DestinationInputValidator.validateDestinationInput(
                        "-30.2345",
                        "180.2344"
                )
        );
    }

    @Test
    public void validationDestinationInput_validInput_statusValid() {
        Assert.assertEquals(
                DestinationInputStatus.VALID,
                DestinationInputValidator.validateDestinationInput(
                        "-40.2345",
                        "80.2344"
                )
        );
    }
}
