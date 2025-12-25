import React from "react";
import { Box, SvgIcon, SvgIconProps } from "@mui/material";

export interface DownArrowIconProps extends SvgIconProps {}

const DownArrowIcon = (props: DownArrowIconProps) => {
  return (
    <SvgIcon style={{ fill: "none" }} {...props}><path fill="#fff" d="M12 22.917c-.24 0-.465-.062-.673-.185-.207-.123-.405-.334-.592-.634L1.746 7.717c-.276-.443-.418-1-.425-1.67-.006-.672.136-1.239.425-1.702.29-.464.641-.696 1.054-.696.413 0 .764.232 1.054.696L12 17.379l8.146-13.034c.277-.443.625-.67 1.045-.68.419-.01.773.216 1.063.68.29.463.435 1.025.435 1.686 0 .66-.145 1.222-.435 1.686l-8.988 14.381c-.188.3-.385.511-.593.634a1.297 1.297 0 0 1-.673.185Z"/></SvgIcon>
  );
};

export default DownArrowIcon;