package com.mycompany.motorph.helpers;

public class Deduction {
    public static double getSSSContribution(double salary) {
        if (salary <= 0)
            return 0;
        double minmin = 3250;
        double maxmax = 24750;
        double[] min = new double[] {
                3250,
                3750,
                4250,
                4750,
                5250,
                5750,
                6250,
                6750,
                7250,
                7750,
                8250,
                8750,
                9250,
                9750,
                10250,
                10750,
                11250,
                11750,
                12250,
                12750,
                13250,
                13750,
                14250,
                14750,
                15250,
                15750,
                16250,
                16750,
                17250,
                17750,
                18250,
                18750,
                19250,
                19750,
                20250,
                20750,
                21250,
                21750,
                22250,
                22750,
                23250,
                23750,
                24250,
        };
        double[] max = new double[] {
                3750,
                4250,
                4750,
                5250,
                5750,
                6250,
                6750,
                7250,
                7750,
                8250,
                8750,
                9250,
                9750,
                10250,
                10750,
                11250,
                11750,
                12250,
                12750,
                13250,
                13750,
                14250,
                14750,
                15250,
                15750,
                16250,
                16750,
                17250,
                17750,
                18250,
                18750,
                19250,
                19750,
                20250,
                20750,
                21250,
                21750,
                22250,
                22750,
                23250,
                23750,
                24250,
                24750
        };

        double[] contribution = new double[] {
                157.50,
                180.00,
                202.50,
                225.00,
                247.50,
                270.00,
                292.50,
                315.00,
                337.50,
                360.00,
                382.50,
                405.00,
                427.50,
                450.00,
                472.50,
                495.00,
                517.50,
                540.00,
                562.50,
                585.00,
                607.50,
                630.00,
                652.50,
                675.00,
                697.50,
                720.00,
                742.50,
                765.00,
                787.50,
                810.00,
                832.50,
                855.00,
                877.50,
                900.00,
                922.50,
                945.00,
                967.50,
                990.00,
                1012.50,
                1035.00,
                1057.50,
                1080.00,
                1102.50,
        };

        if (salary < minmin)
            return 135.00;
        else if (salary >= maxmax)
            return 1125.00;

        for (int i = 0; i < min.length; i++) {
            if (salary >= min[i] && salary < max[i])
                return contribution[i];
        }

        return 0.00;
    }

    public static double getPhilhealthContribution(double salary) {
        if (salary <= 0)
            return 0;
        if (salary <= 10000)
            return 300.00 / 2;
        else if (salary > 10000 && salary < 60000)
            return (salary * 0.03) / 2;
        else if (salary >= 60000)
            return 1800.00 / 2;
        else
            return 0.00;
    }

    public static double getPagibigContribution(double salary) {
        if (salary <= 0)
            return 0;
        if (salary >= 1000 && salary < 1500)
            return Math.min(salary * 0.01, 100);
        else if (salary >= 1500)
            return Math.min(salary * 0.02, 100);
        else
            return 0.00;
    }

    public static double getWithholdingtax(double salary) {
        if (salary <= 0)
            return 0;
        if (salary <= 20832)
            return 0.00;
        else if (salary >= 20833 && salary < 33333)
            return (salary - 20833) * 0.2;
        else if (salary >= 33333 && salary < 66667)
            return 2500 + ((salary - 33333) * 0.25);
        else if (salary >= 66667 && salary < 166667)
            return 10833 + ((salary - 66667) * 0.3);
        else if (salary >= 166667 && salary < 666667)
            return 40833.33 + ((salary - 166667) * 0.32);
        else if (salary >= 666667)
            return 200833.33 + ((salary - 666667) * 0.35);
        else
            return 0.00;
    }
}
