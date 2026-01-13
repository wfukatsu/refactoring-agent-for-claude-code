import { useTranslation } from "react-i18next";

const Error = ({ onClick }) => {
  const {t,i18n} = useTranslation();
  return (
    <div className="flex justify-center items-center h-screen ">
      <div className="bg-red-100 border  text-red-700 px-5 py-5 rounded-lg shadow-md">
        <p>{t("rightClickErrorText")}</p>
        {t("rightClickErrorPleaseText")}
        <button
          className="bg-red-300 py-2 px-5 text-black border rounded-xl"
          onClick={onClick}
        >
          {t("rightClickTryAgainButtonText")}
        </button>
        {t("rightClickTryAgainSomeTimeText")}
        {/* You can add additional error information or links/buttons */}
      </div>
    </div>
  );
};

export default Error;
