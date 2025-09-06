package com.advanced_baseball_stats.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LinearRegressionHelperTest
{
    private val helper = LinearRegressionHelper()

    private val x               : List<Double> = listOf( 1.0, 2.0, 3.0 )
    private val y               : List<Double> = listOf( 2.0, 4.0, 5.0 )
    private val unequalLengthX  : List<Double> = listOf( 1.0, 2.0, 3.0 , 4.0 )
    private val zeroSxxX        : List<Double> = listOf( 1.0, 1.0, 1.0 )

    private val expectedSlope       : Double = 1.5
    private val expectedIntercept   : Double = 0.6666666666666665

    @Test
    fun testLinearRegression_happyPath_expectHappyValues()
    {
        val linearRegression = this.helper.getLinearRegression(this.x, this.y)

        assertNotNull   (linearRegression)
        assertEquals    (linearRegression.getSlope()    , this.expectedSlope    )
        assertEquals    (linearRegression.getIntercept(), this.expectedIntercept)
    }

    @Test
    fun testLinearRegression_unequalLengthInputs_expectNull()
    {
        val linearRegression = this.helper.getLinearRegression(this.unequalLengthX, this.y)

        assertNull(linearRegression)
    }

    @Test
    fun testLinearRegression_sXxIsEqualToZero_expect()
    {
        val linearRegression = this.helper.getLinearRegression(this.zeroSxxX, this.y)

        //TODO: How to handle this use case?
        assertNotNull   (linearRegression)
        assertEquals    (linearRegression.getSlope()    , Double.NaN)
        assertEquals    (linearRegression.getIntercept(), Double.NaN)
    }
}