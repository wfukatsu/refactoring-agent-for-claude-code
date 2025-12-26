import * as React from "react";
import "../ViewAllEventHistory.css";

import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DateTimePicker } from "@mui/x-date-pickers/DateTimePicker";

import 'dayjs/locale/en-gb';



function PickerWithButtonField({ value, setValue }) {
  const containerStyle = {
    width: "80px",
    height: "30px",
    display: "flex",
    alignItems: "center",
    cursor: "pointer",
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en-gb">
      {/* <DemoContainer components={['DatePicker', 'DatePicker']} sx={{width:"277px",height:"30px", backgroundColor: 'red',margin:"5px" ,paddingLeft:1,}}> */}
      {/* <DemoItem> */}
      <DateTimePicker
      views={['year', 'day', 'hours', 'minutes', 'seconds']}
        className="date-picker-text"
        // sx={{ width: "260px " ,height:"25px",backgroundColor: 'blue'}}  //backgroundColor: 'white'
        sx={{ height: "20px", paddingLeft: "10px", paddingRight: "10px" }}
        // slots={{ openPickerIcon: CalanderSVG }}
        slotProps={{
          textField: {
            fullWidth: true,
            size: "small",
            variant: "standard",
            InputProps: {
              disableUnderline: true,
              placeholder: "Select",
            //   color: "black",
            },
          },
        }}
        // popperProps={{
        //   strategy: "fixed" // use this to make the popper position: fixed
        // }}
        timeSteps={{hours: 1, minutes: 1, seconds: 1}}
        // format="DD/MM/YYYY HH:MM:SS"
        value={value}
        onChange={(newValue) => setValue(newValue)}
      />
      {/* </DemoItem> */}
      {/* </DemoContainer> */}
    </LocalizationProvider>
  );
}

export default PickerWithButtonField;
